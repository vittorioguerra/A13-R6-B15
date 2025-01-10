package com.groom.manvsclass.service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystemException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemService {

    private static final Logger logger = LoggerFactory.getLogger(FileSystemService.class);

    private final Map<Path, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    private final ThreadLocal<Map<Path, byte[]>> threadLocalPath = ThreadLocal.withInitial(LinkedHashMap::new);

    // Absolute path
    @Value("${filesystem.classesPath}")
    private String classesFolder;

    @Value("${filesystem.sourceFolder}")
    private String sourceFolder;

    @Value("${filesystem.testsFolder}")
    private String testsFolder;

    public void lockPath(Path path) {
        lockMap.computeIfAbsent(path, p -> new ReentrantLock()).lock();
    }

    public void unlockPath(Path path) {
        ReentrantLock lock = lockMap.get(path);
        if(lock != null) {
            lock.unlock();
            if(!lock.hasQueuedThreads()) {
                lockMap.remove(path, lock);
            }
        }
    }

    public Path saveClass(String className, MultipartFile classFile) throws FileSystemException {
        Path classPath = Paths.get(classesFolder).resolve(className);

        lockPath(classPath);
        try {
            Path path = createFolder(classPath);
    
            path = path.resolve(sourceFolder);
            createFolder(path);
    
            path = saveFile(classFile, path);
    
            logger.info("Class saved successfully: {}", className);
            return path;
        } catch(IOException e) {
            logger.error("Upload of class {} unsuccessful", className);
            deleteAll(className);
            throw new FileSystemException(classFile.getOriginalFilename());
        } finally {
            unlockPath(classPath);
        }

    }

    public Path saveTest(String className, String robotName, MultipartFile testFile) throws FileSystemException {
        Path classPath = Paths.get(classesFolder).resolve(className);

        lockPath(classPath);
        try{
            logger.debug("Saving test of Robot: {}", robotName);
    
            Path path = classPath.resolve(testsFolder).resolve(robotName);
            createFolder(path);
    
            unzip(testFile, path);
    
            logger.info("Test saved successfully: {} | {}", className, robotName);
            return path;
        } catch(IOException e) {
            logger.error("Error occurred while saving: {} | {}", className, robotName);
            deleteAll(className);
            throw new FileSystemException(testFile.getOriginalFilename());
        } finally {
            unlockPath(classPath);
        }
    }

    public Path deleteAll(String className) throws FileSystemException {

        Path path = deleteDirectory(Paths.get(classesFolder).resolve(className));

        logger.info("Class and its tests deleted successfully: {}", className);

        return path;
    }

    public Path deleteClass(String className) throws FileSystemException {

        Path path = deleteDirectory(Paths.get(classesFolder + className).resolve(sourceFolder));

        logger.info("SourceCode deleted successfully: {}", className);

        return path;
    }

    public Path deleteTest(String className, String robotName) throws FileSystemException {

        Path path = deleteDirectory(Paths.get(classesFolder + className).resolve(testsFolder + robotName));

        logger.info("Test deleted successfully: {} | {}", className, robotName);

        return path;
    }

    private Path rawCreateFolder(Path path) throws FileSystemException {
        
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            // Catching exception and rolling back
            logger.warn("Error in creating {}", path.toString());
            throw new FileSystemException(path.toString());
        }

        logger.info("Folder created: {}", path.toString());

        return path;
    }

    public Path createFolder(String path) throws FileSystemException {
        return createFolder(Paths.get(path));
    }

    public Path createFolder(Path path) throws FileSystemException {

        lockPath(path);
        try {
            return rawCreateFolder(path);
        } catch (IOException exception) {
            // Catching exception and rolling back
            deleteDirectory(path);
            throw exception;
        } finally {
            unlockPath(path);
        }
    }

    private Path rawSaveFile(MultipartFile file, Path path) throws FileSystemException {
        Path filePath = path.resolve(file.getOriginalFilename());

        try {
            file.transferTo(filePath);
        } catch (IOException e) {
            logger.warn("Error during transfer of file {}", file.getOriginalFilename());
            throw new FileSystemException(file.getOriginalFilename());
        }

        logger.info("File saved: {}", filePath.toString());

        return filePath;
    }

    public Path saveFile(MultipartFile file, String path) throws FileSystemException {
        return saveFile(file, Paths.get(path));
    }

    public Path saveFile(MultipartFile file, Path path) throws FileSystemException {
        Path filePath = path.resolve(file.getOriginalFilename());

        lockPath(filePath);
        try {
            return rawSaveFile(file, path);
        } catch (IOException e) {
            logger.warn("Error during transfer of file {}", file.getOriginalFilename());
            deleteDirectory(filePath);
            throw new FileSystemException(file.getOriginalFilename());
        } finally {
            unlockPath(filePath);
        }
    }

    public Path unzip(MultipartFile zipFile, Path unzipPath) throws FileSystemException {
        // Verifies the existance of the path, creates it if nonexistent
        if (Files.notExists(unzipPath)) {
            createFolder(unzipPath);
        }

        logger.debug("Beginning extraction of: {}", unzipPath.toString());

        // Creates ZipInputStream from the zipFile
        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry entry;

            // Iterates on the content of the zipFile
            while ((entry = zis.getNextEntry()) != null) {

                // Creates path of extractedFile
                Path extractedPath = unzipPath.resolve(entry.getName()).normalize();

                logger.debug("Extracting: {}", extractedPath.toString());

                // Checks for PathTraversal attacks
                if (!extractedPath.startsWith(unzipPath)) {
                    throw new FileSystemException(extractedPath.toString(), null, "Tentativo di estrarre file fuori dal percorso consentito: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    // If directory, create it
                    Files.createDirectories(extractedPath);
                } else {
                    // If not save file
                    Files.createDirectories(extractedPath.getParent());

                    try (OutputStream os = Files.newOutputStream(extractedPath)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = zis.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                    }
                }

                zis.closeEntry();
            }
        } catch(IOException e) {
            logger.warn("Error during the unzip of {}", zipFile.toString());
            throw new FileSystemException(zipFile.toString());
        }

        logger.info("ZIP file extracted successfully.");
        return unzipPath;
    }

    public Path deleteDirectory(Path directory) throws FileSystemException {
        Map<Path, byte[]> localFiles = threadLocalPath.get();

        //! CHECK: Directory existance
        if (!Files.exists(directory)) {
            logger.warn("The directory {} doesn't exist", directory.toString());
            throw new FileSystemException(directory.toString());
        }

        // Usa un FileVisitor per attraversare ricorsivamente la directory
        lockPath(directory);
        try {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                // Action done when visiting a file
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                    lockPath(file);
                    try {
                        logger.debug("Backup of file: {}", file.toString());
                        localFiles.put(file, Files.readAllBytes(file));
                        logger.debug("Deleting: {}", file.toString());
                        Files.delete(file);
                    } finally {
                        unlockPath(file);
                    }
                    return FileVisitResult.CONTINUE;
                }
    
                // Action done after visiting a directory
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exception) throws IOException {
                    lockPath(dir);
                    try {
                        logger.debug("Back up of folder: {}", dir);
                        localFiles.put(dir, null);
                        logger.debug("Deleting: {}", dir.toString());
                        Files.delete(dir);
                    } finally {
                        unlockPath(dir);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.warn("Deletion of {} gone wrong", directory.toString());

            localFiles.forEach((key, value) -> {
                deleteRollback(key, value);
            });

            throw new FileSystemException(directory.toString());
        } finally {
            unlockPath(directory);
            localFiles.clear();
        }

        logger.info("Deletion completed successfully: {}", directory.toString());

        return directory;
    }

    private void deleteRollback(Path path, byte[] file) {
        try {
            if(file == null){
                createFolder(path);
            } else {
                Files.write(path, file);
            }
        } catch(IOException e) {
            logger.error("Rollback not working: {}", path.toString());
            return;
        }
    }
}
