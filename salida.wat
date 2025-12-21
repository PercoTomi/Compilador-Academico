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
  (global $X:PROG:FUNC (mut i64) (i64.const 0))
  (global $Y:PROG (mut i64) (i64.const 0))
  (global $PROG (mut i64) (i64.const 0))
  ;; Espacio reservado para la declaración de funciones Lambda
  ;; Declaración de funciones estándar
  (func $FUNC:PROG (result i64) 
    global.get $X:PROG:FUNC
    return
    unreachable
  )
  (global $flag_funcion_activa_FUNC:PROG (mut i32) (i32.const 0))
  (func $main (export "main") 
    ;; Inicio de chequeo de llamada recursiva
    global.get $flag_funcion_activa_FUNC:PROG
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
    global.set $flag_funcion_activa_FUNC:PROG
    f64.const 4.0
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
    global.set $X:PROG:FUNC
    call $FUNC:PROG
    i32.const 0
    global.set $flag_funcion_activa_FUNC:PROG
    global.set $Y:PROG
  )
  (data (i32.const 0) "Error: Overflow en la operación.\n")
  (data (i32.const 34) "Error: Perdida de precision al truncar la constante flotante.\n")
  (data (i32.const 96) "Error: No se puede llamar una función desde si misma.\n")
)
