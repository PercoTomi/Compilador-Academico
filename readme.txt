Requisitos:
- Tener Java instalado y accesible desde la terminal (comando: java).
- Tener Node.js instalado y accesible desde la terminal (comando: node).
- Tener WABT (WebAssembly Binary Toolkit) instalado y accesible desde la terminal (comando: wat2wasm).

Este proyecto fue realizado con Node.js en su versión 24.11.1 (LTS) y WABT en su versión 1.0.39

Uso:

1. Compilar el código fuente:
   - Abrir una terminal (cmd) ubicada en la carpeta raíz del proyecto donde está ubicado el archivo "Compilador.jar".
   - Ejecutar:
     java -jar Compilador.jar <archivo_de_entrada>
     (reemplazar <archivo_de_entrada> por el nombre del archivo de código fuente)

   Ejemplo:
     java -jar Compilador.jar codigo.txt

   También es posible utilizar como segundo argumento del jar "-v" ó "--verbose" para imprimir los reportes del analizador
   léxico, el analizador sintáctico y también los subárboles de ejecución de todas las funciones declaradas.

2. Ejecutar el código WebAssembly generado:
   - Convertir el archivo .wat a .wasm:
     wat2wasm -o ejecutable.wasm salida.wat

   - Ejecutar con Node.js:
     node .\executer.js

   Por lo tanto, la secuencia de comandos es:
     wat2wasm -o ejecutable.wasm salida.wat
     node .\executer.js


##------------- Sección de archivos de pruebas -------------##

A continuación se listan los archivos de prueba disponibles en la carpeta "pruebas" con los comandos correspondientes:

1. test_anidamiento.txt
   Descripción: Demuestra el correcto funcionamiento de estructuras de control anidadas,
                incluyendo IF dentro de DO-UNTIL y DO-UNTIL dentro de IF con múltiples niveles.
   Comandos:
     java -jar Compilador.jar pruebas\test_anidamiento.txt
     wat2wasm -o ejecutable.wasm pruebas\salida.wat
     node .\executer.js

2. test_cv y cvr.txt
   Descripción: Demuestra la diferencia entre pasaje de parámetros por copia-valor (CV)
                y copia-valor-resultado (CVR). Con CV la variable original no se modifica,
                con CVR sí.
   Comandos:
     java -jar Compilador.jar "pruebas\test_cv y cvr.txt"
     wat2wasm -o ejecutable.wasm pruebas\salida.wat
     node .\executer.js

3. test_do-until anidados.txt
   Descripción: Demuestra el funcionamiento cuando existen sentencia do-until anidadas.
   Comandos:
     java -jar Compilador.jar "pruebas\test_do-until anidados.txt"
     wat2wasm -o ejecutable.wasm pruebas\salida.wat
     node .\executer.js

4. test_funciones anidadas y uso de variables del padre.txt
   Descripción: Prueba de funciones anidadas donde una función hija accede a variables
                definidas en la función padre y en el ámbito global.
   Comandos:
     java -jar Compilador.jar "pruebas\test_funciones anidadas y uso de variables del padre.txt"
     wat2wasm -o ejecutable.wasm pruebas\salida.wat
     node .\executer.js

5. test_funciones lambda.txt
   Descripción: Demuestra el uso de funciones lambda, incluyendo lambdas con una y múltiples sentencias.
   Comandos:
     java -jar Compilador.jar "pruebas\test_funciones lambda.txt"
     wat2wasm -o ejecutable.wasm pruebas\salida.wat
     node .\executer.js

6. test_if anidados con returns en todos los caminos.txt
   Descripción: Demuestra estructuras IF anidadas con sentencias return en todos
                los caminos de ejecución posibles.
   Comandos:
     java -jar Compilador.jar "pruebas\test_if anidados con returns en todos los caminos.txt"
     wat2wasm -o ejecutable.wasm pruebas\salida.wat
     node .\executer.js

7. test_if y do con sentencia unica.txt
   Descripción: Demuestra el uso de IF y DO-UNTIL con una única sentencia (sin llaves).
   Comandos:
     java -jar Compilador.jar "pruebas\test_if y do con sentencia unica.txt"
     wat2wasm -o ejecutable.wasm pruebas\salida.wat
     node .\executer.js

8. test_if-else con return en todos los caminos.txt
   Descripción: Similar al test de IF anidados, muestra funciones con return
                en todas las ramas IF-ELSE.
   Comandos:
     java -jar Compilador.jar "pruebas\test_if-else con return en todos los caminos.txt"
     wat2wasm -o ejecutable.wasm pruebas\salida.wat
     node .\executer.js

9. test_multiples parametros en distinto orden.txt
    Descripción: Demuestra que los parámetros de una función pueden pasarse
                 en cualquier orden dado que el lenguaje utiliza pasaje por nombre.
    Comandos:
      java -jar Compilador.jar "pruebas\test_multiples parametros en distinto orden.txt"
      wat2wasm -o ejecutable.wasm pruebas\salida.wat
      node .\executer.js

10. test_operaciones aritmeticas.txt
    Descripción: Prueba de todas las operaciones aritméticas soportadas.
    Comandos:
      java -jar Compilador.jar "pruebas\test_operaciones aritmeticas.txt"
      wat2wasm -o ejecutable.wasm pruebas\salida.wat
      node .\executer.js

11. test_todos los comparadores.txt
    Descripción: Prueba de todos los operadores de comparación: >, <, >=, <=, ==, =!
    Comandos:
      java -jar Compilador.jar "pruebas\test_todos los comparadores.txt"
      wat2wasm -o ejecutable.wasm pruebas\salida.wat
      node .\executer.js

12. test_uso de trunc.txt
    Descripción: Demuestra el uso válido de la función TRUNC para convertir
                 valores float a long truncando la parte decimal.
    Comandos:
      java -jar Compilador.jar "pruebas\test_uso de trunc.txt"
      wat2wasm -o ejecutable.wasm pruebas\salida.wat
      node .\executer.js


##------------- Archivos de prueba con errores -------------##

13. error_alcance.txt
    Descripción: Genera un error en tiempo de compilación al intentar acceder a una variable
                 local (VARLOCAL) definida en otra función, violando las reglas de ámbito.
    Comando:
      java -jar Compilador.jar pruebas\error_alcance.txt

14. error_runtime_overflow.txt
    Descripción: Genera un error en tiempo de ejecución por overflow aritmético al sumar 1
                 al valor máximo representable en un long (2147483647).
    Comandos:
      java -jar Compilador.jar pruebas\error_runtime_overflow.txt
      wat2wasm -o ejecutable.wasm pruebas\salida.wat
      node .\executer.js

15. error_runtime_recursive.txt
    Descripción: Genera un error en tiempo de ejecución al intentar una llamada recursiva
                 directa (función FACTORIAL que se llama a sí misma), lo cual no está
                 permitido en el lenguaje.
    Comandos:
      java -jar Compilador.jar pruebas\error_runtime_recursive.txt
      wat2wasm -o ejecutable.wasm pruebas\salida.wat
      node .\executer.js

16. error_runtime_trunc.txt
    Descripción: Genera un error en tiempo de ejecución al usar la función trunc() con un
                 valor que tiene parte decimal (3.14), ya que se considera pérdida de precisión.
    Comandos:
      java -jar Compilador.jar pruebas\error_runtime_trunc.txt
      wat2wasm -o ejecutable.wasm pruebas\salida.wat
      node .\executer.js