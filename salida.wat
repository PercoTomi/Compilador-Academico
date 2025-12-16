(module
  (import "env" "mem" (memory 1))
  (import "env" "print_i64" (func $print_i64 (param i64)))
  (import "env" "print_string" (func $print_string (param i32 i32)))
  ;; variables auxiliares para chequeo de errores 
  (global $aux_overflow (mut i64) (i64.const 0))
  (global $aux_float (mut f64) (f64.const 0.0))
  ;; Declaraciones de tipo de función lambda y de su tabla 
  (type $funcLambda (func (param i64)))
  (table 0 funcref)
  ;; Declaración de variables globales 
  (global $B:PROGRAMA:CALCULAR (mut i64) (i64.const 0))
  (global $PROGRAMA (mut i64) (i64.const 0))
  (global $A:PROGRAMA:CALCULAR (mut i64) (i64.const 0))
  (global $R:PROGRAMA (mut i64) (i64.const 0))
  (global $C:PROGRAMA:CALCULAR (mut i64) (i64.const 0))
  ;; Espacio reservado para la declaración de funciones Lambda
  ;; Declaración de funciones estándar
  (func $CALCULAR:PROGRAMA (result i64) 
    global.get $A:PROGRAMA:CALCULAR
    global.get $B:PROGRAMA:CALCULAR
    i64.mul
    global.get $C:PROGRAMA:CALCULAR
    i64.add
    ;; INICIO CHEQUEO OVERFLOW 
    global.set $aux_overflow
    global.get $aux_overflow
    i64.const 2147483647
    i64.gt_s
    (if
      (then
        i32.const 0
        i32.const 34
        call $print_string
        unreachable
      )
    )
    global.get $aux_overflow
    i64.const -2147483648
    i64.lt_s
    (if
      (then
        i32.const 0
        i32.const 34
        call $print_string
        unreachable
      )
    )
    ;; FIN CHEQUEO OVERFLOW 
    global.get $aux_overflow
    return
    unreachable
  )
  (global $flag_funcion_activa_CALCULAR:PROGRAMA (mut i32) (i32.const 0))
  (func $main (export "main") 
    ;; Inicio de chequeo de llamada recursiva
    global.get $flag_funcion_activa_CALCULAR:PROGRAMA
    (if
      (then
        i32.const 96
        i32.const 55
        call $print_string
        unreachable
      )
    )
    ;; Fin de chequeo de llamada recursiva
    i32.const 1
    global.set $flag_funcion_activa_CALCULAR:PROGRAMA
    i64.const 2
    global.set $A:PROGRAMA:CALCULAR
    i64.const 3
    global.set $B:PROGRAMA:CALCULAR
    i64.const 4
    global.set $C:PROGRAMA:CALCULAR
    call $CALCULAR:PROGRAMA
    i32.const 0
    global.set $flag_funcion_activa_CALCULAR:PROGRAMA
    global.set $R:PROGRAMA
    global.get $R:PROGRAMA
    call $print_i64
    ;; Inicio de chequeo de llamada recursiva
    global.get $flag_funcion_activa_CALCULAR:PROGRAMA
    (if
      (then
        i32.const 96
        i32.const 55
        call $print_string
        unreachable
      )
    )
    ;; Fin de chequeo de llamada recursiva
    i32.const 1
    global.set $flag_funcion_activa_CALCULAR:PROGRAMA
    i64.const 2
    global.set $A:PROGRAMA:CALCULAR
    i64.const 3
    global.set $B:PROGRAMA:CALCULAR
    i64.const 4
    global.set $C:PROGRAMA:CALCULAR
    call $CALCULAR:PROGRAMA
    i32.const 0
    global.set $flag_funcion_activa_CALCULAR:PROGRAMA
    global.set $R:PROGRAMA
    global.get $R:PROGRAMA
    call $print_i64
  )
  (data (i32.const 0) "Error: Overflow en la operación.\n")
  (data (i32.const 34) "Error: Perdida de precision al truncar la constante flotante.\n")
  (data (i32.const 96) "Error: No se puede llamar una función desde si misma.\n")
)
