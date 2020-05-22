package com.sythesystems.jeb;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Blueprint {
    
    private Set<String> writablePaths = new HashSet<>();
    
    public Blueprint (String... paths) {
        writablePaths.add("/");
        writablePaths.addAll(Arrays.asList(paths));
    }
    
    public boolean shouldWrite(String path) {
        for(String writablePath: writablePaths) {
            if(writablePath.startsWith(path)) {
                return true;
            }
        }
        return false;
    }
}
