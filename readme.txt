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

1. test_completo.txt
   Descripción: Prueba integral que demuestra el correcto funcionamiento
                de las estructuras sintácticas del lenguaje.
   Comandos:
     java -jar Compilador.jar pruebas\test_completo.txt
     wat2wasm -o ejecutable.wasm pruebas\salida.wat
     node .\executer.js

2. test_anidamiento.txt
   Descripción: Demuestra el correcto funcionamiento de estructuras de control anidadas,
                incluyendo IF dentro de DO-UNTIL y DO-UNTIL dentro de IF con múltiples niveles.
   Comandos:
     java -jar Compilador.jar pruebas\test_anidamiento.txt
     wat2wasm -o ejecutable.wasm pruebas\salida.wat
     node .\executer.js

3. error_alcance.txt
   Descripción: Genera un error en tiempo de compilación al intentar acceder a una variable
                local (VARLOCAL) definida en otra función, violando las reglas de ámbito.
   Comando:
     java -jar Compilador.jar pruebas\error_alcance.txt

4. error_runtime_overflow.txt
   Descripción: Genera un error en tiempo de ejecución por overflow aritmético al sumar 1
                al valor máximo representable en un long (2147483647).
   Comando:
     java -jar Compilador.jar pruebas\error_runtime_overflow.txt

5. error_runtime_recursive.txt
   Descripción: Genera un error en tiempo de ejecución al intentar una llamada recursiva
                directa (función FACTORIAL que se llama a sí misma), lo cual no está
                permitido en el lenguaje.
   Comando:
     java -jar Compilador.jar pruebas\error_runtime_recursive.txt

6. error_runtime_trunc.txt
   Descripción: Genera un error en tiempo de ejecución al usar la función trunc() con un
                valor que tiene parte decimal (3.14), ya que se considera pérdida de precisión.
   Comando:
     java -jar Compilador.jar pruebas\error_runtime_trunc.txt