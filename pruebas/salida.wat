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
  (global $PARAM:PROGRAMATRUNC:FUNCIONAUX (mut i64) (i64.const 0))
  (global $RESULTADO:PROGRAMATRUNC (mut i64) (i64.const 0))
  (global $PROGRAMATRUNC (mut i64) (i64.const 0))
  ;; Espacio reservado para la declaración de funciones Lambda
  ;; Declaración de funciones estándar
  (func $FUNCIONAUX:PROGRAMATRUNC (result i64) 
    global.get $PARAM:PROGRAMATRUNC:FUNCIONAUX
    return
    unreachable
  )
  (global $flag_funcion_activa_FUNCIONAUX:PROGRAMATRUNC (mut i32) (i32.const 0))
  (func $main (export "main") 
    ;; Inicio de chequeo de llamada recursiva
    global.get $flag_funcion_activa_FUNCIONAUX:PROGRAMATRUNC
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
    global.set $flag_funcion_activa_FUNCIONAUX:PROGRAMATRUNC
    f64.const 3.14
    global.set $aux_float
    global.get $aux_float
    global.get $aux_float
    f64.trunc
    f64.ne
    (if
      (then
        i32.const 34
        i32.const 62
        call $print_string
        unreachable
      )
    )
    global.get $aux_float
    f64.const 2147483647.0
    f64.gt
    (if
      (then
        i32.const 34
        i32.const 62
        call $print_string
        unreachable
      )
    )
    global.get $aux_float
    f64.const -2147483648.0
    f64.lt
    (if
      (then
        i32.const 34
        i32.const 62
        call $print_string
        unreachable
      )
    )
    global.get $aux_float
    i64.trunc_f64_s
    global.set $PARAM:PROGRAMATRUNC:FUNCIONAUX
    call $FUNCIONAUX:PROGRAMATRUNC
    i32.const 0
    global.set $flag_funcion_activa_FUNCIONAUX:PROGRAMATRUNC
    global.set $RESULTADO:PROGRAMATRUNC
  )
  (data (i32.const 0) "Error: Overflow en la operación.\n")
  (data (i32.const 34) "Error: Perdida de precision al truncar la constante flotante.\n")
  (data (i32.const 96) "Error: No se puede llamar una función desde si misma.\n")
)
