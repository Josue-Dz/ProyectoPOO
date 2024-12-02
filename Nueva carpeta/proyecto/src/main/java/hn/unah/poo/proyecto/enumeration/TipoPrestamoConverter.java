package hn.unah.poo.proyecto.enumeration;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoPrestamoConverter implements AttributeConverter<TipoPrestamo, Character> {

    @Override
    public Character convertToDatabaseColumn(TipoPrestamo tipoPrestamo) {
        if (tipoPrestamo == null){
            return null;
        }
        return tipoPrestamo.getCode();
    }

    @Override
    public TipoPrestamo convertToEntityAttribute(Character dbData) {
        if (dbData == null){
            return null;
        }
        return TipoPrestamo.fromCode(dbData);
    }
    

}
