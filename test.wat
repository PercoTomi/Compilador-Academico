(module
  (import "env" "mem" (memory 1))
  (import "env" "print_i64" (func $print_i64 (param i64)))
  (import "env" "print_string" (func $print_string (param i32 i32)))
  ;; variables auxiliares para chequeo de errores 
  (global $aux_overflow (mut i64) (i64.const 0))
  (global $aux_float (mut f64) (f64.const 0.0))
  ;; Declaraciones de tipo de función lambda y de su tabla 
  (type $funcLambda (func (param i64) (result i64)))
  (table 2 funcref)
  ;; Declaración de variables globales 
  (global $P (mut i64) (i64.const 0))
  (global $L1:P:H (mut i64) (i64.const 0))
  (global $L1:P:F (mut i64) (i64.const 0))
  ;; Espacio reservado para la declaración de funciones Lambda
  (func $lambda_func_1 (param i64) (result i64) 
    i64.const 1000
  )
  (elem (i32.const 1) $lambda_func_1)
  (func $lambda_func_0 (param i64) (result i64) 
    local.get 0
    call $print_i64
    ;; Inicio de chequeo de llamada recursiva
    global.get $flag_funcion_activa_H:P
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
    global.set $flag_funcion_activa_H:P
    i64.const 1   ;; Indice en la tabla a guardar en parámetro formal
    global.set $L1:P:H
    call $H:P
    i32.const 0
    global.set $flag_funcion_activa_H:P
    local.set 0
    local.get 0
    call $print_i64
    local.get 0
  )
  (elem (i32.const 0) $lambda_func_0)
  ;; Declaración de funciones estándar
  (func $F:P (result i64) 
    i64.const 4
    global.get $L1:P:F
    i32.wrap_i64
    call_indirect (type $funcLambda)
  )
  (global $flag_funcion_activa_F:P (mut i32) (i32.const 0))
  (func $H:P (result i64) 
    i64.const 4
    global.get $L1:P:H
    i32.wrap_i64
    call_indirect (type $funcLambda)
  )
  (global $flag_funcion_activa_H:P (mut i32) (i32.const 0))
  (func $main (export "main") 
    i64.const 1
    ;; Inicio de chequeo de llamada recursiva
    global.get $flag_funcion_activa_F:P
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
    global.set $flag_funcion_activa_F:P
    i64.const 0   ;; Indice en la tabla a guardar en parámetro formal
    global.set $L1:P:H
    call $F:P
    i32.const 0
    global.set $flag_funcion_activa_H:P
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
    call $print_i64
  )
  (data (i32.const 0) "Error: Overflow en la operación.\n")
  (data (i32.const 34) "Error: Perdida de precision al truncar la constante flotante.\n")
  (data (i32.const 96) "Error: No se puede llamar una función desde si misma.\n")
)
