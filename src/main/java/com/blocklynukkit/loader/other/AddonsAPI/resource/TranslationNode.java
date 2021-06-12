package com.blocklynukkit.loader.other.AddonsAPI.resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TranslationNode extends ResourceNode{
    public Map<String, String> en_US_translations = new HashMap<>();
    public Map<String, String> zh_CN_translations = new HashMap<>();

    @Override
    public ResourceNode write(ZipOutputStream mcpack) {
        super.write(mcpack);
        try {
            mcpack.putNextEntry(new ZipEntry("texts/en_US.lang"));
            StringBuilder output = new StringBuilder();
            output.append(' ');
            for(Map.Entry<String, String> entry:en_US_translations.entrySet()){
                output.append(entry.getKey()).append('=').append(entry.getValue()).append('\n');
            }
            mcpack.write(output.toString().getBytes(StandardCharsets.UTF_8));
            mcpack.closeEntry();

            mcpack.putNextEntry(new ZipEntry("texts/zh_CN.lang"));
            StringBuilder output2 = new StringBuilder();
            output2.append(' ');
            for(Map.Entry<String, String> entry:zh_CN_translations.entrySet()){
                output2.append(entry.getKey()).append('=').append(entry.getValue()).append('\n');
            }
            mcpack.write(output2.toString().getBytes(StandardCharsets.UTF_8));
            mcpack.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public TranslationNode addEnglishTranslation(String source, String to){
        en_US_translations.put(source, to);
        return this;
    }

    public TranslationNode addChineseTranslation(String source, String to){
        zh_CN_translations.put(source, to);
        return this;
    }

    @Override
    public int hashCode() {
        int hash = Integer.MIN_VALUE;
        for(Map.Entry<String, String> entry:en_US_translations.entrySet()){
            hash += entry.getKey().hashCode();
            hash += entry.getValue().hashCode();
        }
        for(Map.Entry<String, String> entry:zh_CN_translations.entrySet()){
            hash += entry.getKey().hashCode();
            hash += entry.getValue().hashCode();
        }
        return hash;
    }
}
