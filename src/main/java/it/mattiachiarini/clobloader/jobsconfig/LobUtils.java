package it.mattiachiarini.clobloader.jobsconfig;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LobUtils {


    public static Set<String> listFilesUsingJavaIO(String dir) {
        File filedir = new File(dir);
        if(!filedir.isDirectory()){
            throw new InvalidParameterException("Errore il parametro non è una directory valida");
        }
        File[] files = filedir.listFiles();
        return Stream.of(files).filter(file -> !file.isDirectory()).map(File::getName).collect(Collectors.toSet());
    }

}
