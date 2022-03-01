package com.mauricio.sync.client;

import com.mauricio.sync.events.EventEmitter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class SyncFileObserver extends EventEmitter<ISyncFileObserverListener> implements ISyncFileObserver, Runnable{
    private Map<String, Boolean> syncStatusMap; // filename, register status
    private File observedDir;
    private boolean running;

    public SyncFileObserver(){
        syncStatusMap = new HashMap<>();
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public void setObservedDir(File dir) {
        observedDir = dir;
    }

    @Override
    public File getObservedDir() {
        return observedDir;
    }

    @Override
    public File getFile(String filename) {
        String path = Paths.get(observedDir.getPath(), filename).toString();
        return new File(path);
    }

    @Override
    public String getFullPath(String filename) {
        return getFile(filename).getAbsolutePath();
    }

    @Override
    public boolean doesFileExist(String filename) {
        File file = getFile(filename);
        return file.exists() && file.isFile();
    }

    @Override
    public boolean doesDirectoryExist(String dirname) {
        File file = getFile(dirname);
        return file.exists() && file.isDirectory();
    }

    @Override
    public File[] getFiles() {
        return observedDir.listFiles();
    }

    @Override
    public Map<String, Boolean> getSyncStatusMap() {
        return syncStatusMap;
    }

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
                            for (ISyncFileObserverListener listener : getListeners()) {
                                listener.onFileAdded(relativePathTo(file), file.isDirectory());
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
                            for (ISyncFileObserverListener listener : getListeners()) {
                                listener.onFileRemoved(entry.getKey(), syncStatusMap.get(entry.getKey()));
                            }
                            syncMapEntryIter.remove();
                        }
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

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

    public String relativePathTo(File file) {
        String relativePath = observedDir.toPath().relativize(file.toPath()).toString();
        return relativePath;
    }

    public List<String> deepListFiles(File dir, List<String> paths, String filter){
        for (File file : dir.listFiles()){
            if (file.isFile()){
                if (filter.length() > 0 && !file.getName().equals(filter)){
                    continue;
                }
                String relativePath = relativePathTo(file);
                paths.add(relativePath);
            }
            if (file.isDirectory()){
                deepListFiles(file, paths, filter);
            }
        }
        return paths;
    }
}
