package com.mauricio.sync.model.client;

import com.mauricio.sync.model.events.EventEmitter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Mauricio Rumštajn
 */
public class SyncFileObserver extends EventEmitter<ISyncFileObserverListener> implements ISyncFileObserver, Runnable{
    private Map<String, Boolean> syncStatusMap; // filename, register status
    private Map<String, Boolean> addCache;     //  filename, isDir
    private Map<String, Boolean> removeCache; //   filename, isDir
    private File observedDir;
    private boolean running;

    public SyncFileObserver(){
        syncStatusMap = new HashMap<>();
        addCache = new HashMap<>();
        removeCache = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        running = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setObservedDir(File dir) {
        observedDir = dir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getObservedDir() {
        return observedDir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getFile(String filename) {
        String path = Paths.get(observedDir.getPath(), filename).toString();
        return new File(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFullPath(String filename) {
        return getFile(filename).getAbsolutePath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean doesFileExist(String filename) {
        File file = getFile(filename);
        return file.exists() && file.isFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean doesDirectoryExist(String dirname) {
        File file = getFile(dirname);
        return file.exists() && file.isDirectory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File[] getFiles() {
        return observedDir.listFiles();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Boolean> getSyncStatusMap() {
        return syncStatusMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        running = true;
        while (running){
            if (observedDir != null) {
                if (observedDir.exists()) {
                    for (File file : observedDir.listFiles()) {
                        // check for new files
                        if (!syncStatusMap.containsKey(file.getName())) {
                            syncStatusMap.put(file.getName(), false);
                            addCache.put(file.getName(), file.isDirectory());
                            for (ISyncFileObserverListener listener : getListeners()) {
                                //listener.onFileAdded(relativePathTo(file), file.isDirectory());
                                listener.onFileAdded(file.getName(), file.isDirectory());
                            }
                        }
                    }
                    // check if any files were removed
                    Iterator<Map.Entry<String, Boolean>> syncMapEntryIter = syncStatusMap.entrySet().iterator();
                    while (syncMapEntryIter.hasNext()){
                        Map.Entry<String, Boolean> entry = syncMapEntryIter.next();
                        boolean removed = true;
                        for (File tfile : observedDir.listFiles()){
                            if (tfile.getName().equals(entry.getKey())){
                                removed = false;
                            }
                        }
                        if (removed){
                            /*String relPath = deepListFiles(observedDir, new ArrayList<>(),
                                    entry.getKey(), false).get(0);
                            boolean isDir = new File(getFullPath(relPath)).isDirectory();*/
                            boolean isDir = new File(entry.getKey()).isDirectory();
                            removeCache.put(entry.getKey(), isDir);
                            syncMapEntryIter.remove();
                            for (ISyncFileObserverListener listener : getListeners()) {
                                listener.onFileRemoved(entry.getKey(), entry.getValue());
                            }
                        }
                    }
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * {@inheritDoc}
     * @throws IOException
     */
    public void writeBuffer(byte[] buff, String path) throws IOException {
        String fullPath = getFullPath(path);
        File file = new File(fullPath);
        if (!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        FileOutputStream out = new FileOutputStream(file);
        out.write(buff);
        out.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Boolean> getAddCache() {
        return addCache;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Boolean> getRemoveCache() {
        return removeCache;
    }

    /**
     * {@inheritDoc}
     */
    public String relativePathTo(File file) {
        String relativePath = observedDir.toPath().relativize(file.toPath()).toString();
        return relativePath;
    }

    /**
     * {@inheritDoc}
     */
    public String relativePathTo(String path) {
        String relativePath = observedDir.toPath().relativize(Paths.get(path)).toString();
        return relativePath;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> deepListFiles(File dir, List<String> paths, String filter, boolean includeDirs){
        for (File file : dir.listFiles()){
            if (file.isFile() || includeDirs){
                if (filter.length() > 0 && !file.getName().equals(filter)){
                    continue;
                }
                String relativePath = relativePathTo(file);
                paths.add(relativePath);
            }
            if (file.isDirectory()){
                deepListFiles(file, paths, filter, includeDirs);
            }
        }
        return paths;
    }


}
