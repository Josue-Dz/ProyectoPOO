package hn.unah.poo.proyecto.enumeration;

public enum TipoPrestamo {
    
    V('V'), 
    P('P'), 
    H('H');

    private final char code;

    TipoPrestamo(char code){
        this.code = code;
    }

    public char getCode(){
        return this.code;
    }

    public static TipoPrestamo fromCode(char code){
        for (TipoPrestamo tipo : TipoPrestamo.values()) {
            if (tipo.code == code){
                return tipo;
            }
        }
        throw new IllegalArgumentException("Este tipo de prestamo no existe! " + code);
    }
}
