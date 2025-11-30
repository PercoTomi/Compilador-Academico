import Simbolos.*;

public class ArbolSintactico {
    private Simbolo refSimbolo;
    private String categoria; 
    private ArbolSintactico left;
    private ArbolSintactico right;

    // Constructor para nodos hoja
    public ArbolSintactico(Simbolo refSimbolo) {
        this.refSimbolo = refSimbolo;
        this.categoria = null;
        this.left = null;
        this.right = null;
    }

    // Constructor para nodos intermedios
    public ArbolSintactico(String categoria, ArbolSintactico left, ArbolSintactico right) {
        this.refSimbolo = null;
        this.categoria = categoria;
        this.left = left;
        this.right = right;
    }

    public Simbolo getRefSimbolo() {
        if (refSimbolo == null) {
            throw new IllegalStateException("Este nodo no es una hoja y no tiene un símbolo asociado.");
        }
        return refSimbolo;
    }

    public String getCategoria() {
        return categoria;
    }

    public ArbolSintactico getLeft() {
        return left;
    }

    public ArbolSintactico getRight() {
        return right;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setUltimoDerecho(ArbolSintactico nuevoDerecho) {
        if(this.right == null) {
            this.right = nuevoDerecho;
        } else {
            this.right.setUltimoDerecho(nuevoDerecho);
        }
    }

 // Método público para iniciar la impresión
    public void imprimirArbol() {
        imprimirRecursivo(this, "", true);
    }

    // Método auxiliar recursivo para formatear la salida
    private void imprimirRecursivo(ArbolSintactico nodo, String prefijo, boolean esUltimo) {
        if (nodo == null) {
            return;
        }

        // Determinar qué mostrar: Categoría (nodo interno) o Lexema (hoja)
        String contenido;
        if (nodo.categoria != null) {
            contenido = nodo.categoria;
        } else if (nodo.refSimbolo != null) {
            contenido = "Hoja: " + nodo.refSimbolo.getLexema();
        } else {
            contenido = "Nodo Vacío";
        }

        // Imprimir la estructura visual
        System.out.println(prefijo + (esUltimo ? "└── " : "├── ") + contenido);

        // Calcular el prefijo para los hijos
        String nuevoPrefijo = prefijo + (esUltimo ? "    " : "│   ");

        // Llamadas recursivas
        // Si tiene ambos hijos
        if (nodo.left != null && nodo.right != null) {
            imprimirRecursivo(nodo.left, nuevoPrefijo, false);
            imprimirRecursivo(nodo.right, nuevoPrefijo, true);
        } 
        // Si solo tiene hijo izquierdo
        else if (nodo.left != null) {
            imprimirRecursivo(nodo.left, nuevoPrefijo, true);
        } 
        // Si solo tiene hijo derecho
        else if (nodo.right != null) {
            imprimirRecursivo(nodo.right, nuevoPrefijo, true);
        }
    }

    public void adoptarNodoIntermedio (ArbolSintactico nodo) {
        this.categoria = nodo.getCategoria();
        this.left = nodo.getLeft();
        this.right = nodo.getRight();
    }

}



