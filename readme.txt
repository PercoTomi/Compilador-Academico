Archivos de prueba:

prueba.txt:
- demuestra lo detectado por el analizador léxico y sintáctico ante un programa correctamente escrito.

erroresSintacticos.txt:
- demuestra los errores sintácticos que es capaz de detectar el analizador sintáctico.

pruebaLexico.txt: 
- verifica el correcto funcionamiento de constantes con el primer y último valor dentro del rango.
- constante positiva que se va del rango por una unidad.
- flotantes con parte entera y sin parte decimal, con parte decimal y sin parte entera, sin exponente y con exponente, tanto positivo como negativo.
- identificadores de menos y más de 20 caracteres.
- identificadores con letras, dígitos y "%".
- comentarios multilinea.

cadenaMalEscrita.txt:
- Este archivo pone a prueba la reaccion del lexer cuando se le pasa un archivo en donde se intenta ejecutar un print que tiene como argumento una
cadena multilinea sin su delimitador final.

constanteFueraDeRango:
- Demuestra el error del léxico al detectar una constante fuera de rango.

idInvalido1.txt:
- Demuestran los errores que ocurren cuando se intenta nombrar una variable de forma invalida en el caso desde su primer caracter.

idInvalido2.txt:
- Demuestra lo que ocurre al intentar asignar un nombre valido al ID cuando sus primeros caracteres son válidos (Donde el léxico toma el Token valido utilizando dichos caracteres). 