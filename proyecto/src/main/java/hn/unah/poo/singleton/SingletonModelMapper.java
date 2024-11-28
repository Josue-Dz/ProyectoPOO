package hn.unah.poo.singleton;

import org.modelmapper.ModelMapper;

public class SingletonModelMapper {

    private static ModelMapper modelMapper;

    private SingletonModelMapper(){

    }

    public static ModelMapper getModelMapperInstance(){
        if(modelMapper == null){
            modelMapper = new ModelMapper();
        }

        return modelMapper;
    }

}
