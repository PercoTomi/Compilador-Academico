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
  (global $B:PROGRAMACOMPLETO (mut i64) (i64.const 0))
  (global $X:PROGRAMACOMPLETO:COMPARARYDEVOLVERMAY (mut i64) (i64.const 0))
  (global $Y:PROGRAMACOMPLETO:COMPARARYDEVOLVERMAY (mut i64) (i64.const 0))
  (global $ACUM:PROGRAMACOMPLETO:FACTORIAL (mut i64) (i64.const 0))
  (global $C:PROGRAMACOMPLETO (mut i64) (i64.const 0))
  (global $VALOR:PROGRAMACOMPLETO:APLICAROPERACION (mut i64) (i64.const 0))
  (global $I:PROGRAMACOMPLETO:FACTORIAL (mut i64) (i64.const 0))
  (global $PROGRAMACOMPLETO (mut i64) (i64.const 0))
  (global $OP:PROGRAMACOMPLETO:APLICAROPERACION (mut i64) (i64.const 0))
  (global $N:PROGRAMACOMPLETO:FACTORIAL (mut i64) (i64.const 0))
  (global $RESULTADO:PROGRAMACOMPLETO (mut i64) (i64.const 0))
  (global $A:PROGRAMACOMPLETO (mut i64) (i64.const 0))
  ;; Espacio reservado para la declaración de funciones Lambda
  (func $lambda_func_0 (param i64) (result i64) 
    local.get 0
    i64.const 2
    i64.mul
    return
  )
  (elem (i32.const 0) $lambda_func_0)
  (func $lambda_func_1 (param i64) (result i64) 
    local.get 0
    local.get 0
    i64.mul
    return
  )
  (elem (i32.const 1) $lambda_func_1)
  ;; Declaración de funciones estándar
  (func $FACTORIAL:PROGRAMACOMPLETO (result i64) 
    i64.const 1
    global.set $ACUM:PROGRAMACOMPLETO:FACTORIAL
    i64.const 1
    global.set $I:PROGRAMACOMPLETO:FACTORIAL
    (block $salidaDoUntil1
      (loop $loopDoUntil1
      global.get $ACUM:PROGRAMACOMPLETO:FACTORIAL
      global.get $I:PROGRAMACOMPLETO:FACTORIAL
      i64.mul
      global.set $ACUM:PROGRAMACOMPLETO:FACTORIAL
      global.get $I:PROGRAMACOMPLETO:FACTORIAL
      i64.const 1
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
      global.set $I:PROGRAMACOMPLETO:FACTORIAL
      global.get $I:PROGRAMACOMPLETO:FACTORIAL
      global.get $N:PROGRAMACOMPLETO:FACTORIAL
      i64.le_s
      br_if $loopDoUntil1
      br $salidaDoUntil1
      )
    )
    global.get $ACUM:PROGRAMACOMPLETO:FACTORIAL
    return
    unreachable
  )
  (global $flag_funcion_activa_FACTORIAL:PROGRAMACOMPLETO (mut i32) (i32.const 0))
  (func $APLICAROPERACION:PROGRAMACOMPLETO (result i64) 
    global.get $VALOR:PROGRAMACOMPLETO:APLICAROPERACION
    global.get $OP:PROGRAMACOMPLETO:APLICAROPERACION
    i32.wrap_i64
    call_indirect (type $funcLambda)
    return
    unreachable
  )
  (global $flag_funcion_activa_APLICAROPERACION:PROGRAMACOMPLETO (mut i32) (i32.const 0))
  (func $COMPARARYDEVOLVERMAY:PROGRAMACOMPLETO (result i64) 
    global.get $X:PROGRAMACOMPLETO:COMPARARYDEVOLVERMAY
    global.get $Y:PROGRAMACOMPLETO:COMPARARYDEVOLVERMAY
    i64.lt_s
    (if 
      (then 
        global.get $X:PROGRAMACOMPLETO:COMPARARYDEVOLVERMAY
        return
      ) 
      (else 
        global.get $Y:PROGRAMACOMPLETO:COMPARARYDEVOLVERMAY
        return
      ) 
    ) 
    unreachable
  )
  (global $flag_funcion_activa_COMPARARYDEVOLVERMAY:PROGRAMACOMPLETO (mut i32) (i32.const 0))
  (func $main (export "main") 
    i64.const 10
    global.set $A:PROGRAMACOMPLETO
    i64.const 20
    global.set $B:PROGRAMACOMPLETO
    i64.const -5
    global.set $C:PROGRAMACOMPLETO
    i32.const 151
    i32.const 15
    call $print_string
    ;; Inicio de chequeo de llamada recursiva
    global.get $flag_funcion_activa_FACTORIAL:PROGRAMACOMPLETO
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
    global.set $flag_funcion_activa_FACTORIAL:PROGRAMACOMPLETO
    i64.const 5
    global.set $N:PROGRAMACOMPLETO:FACTORIAL
    call $FACTORIAL:PROGRAMACOMPLETO
    i32.const 0
    global.set $flag_funcion_activa_FACTORIAL:PROGRAMACOMPLETO
    global.set $RESULTADO:PROGRAMACOMPLETO
    global.get $RESULTADO:PROGRAMACOMPLETO
    call $print_i64
    i32.const 166
    i32.const 18
    call $print_string
    ;; Inicio de chequeo de llamada recursiva
    global.get $flag_funcion_activa_COMPARARYDEVOLVERMAY:PROGRAMACOMPLETO
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
    global.set $flag_funcion_activa_COMPARARYDEVOLVERMAY:PROGRAMACOMPLETO
    global.get $A:PROGRAMACOMPLETO
    global.set $X:PROGRAMACOMPLETO:COMPARARYDEVOLVERMAY
    global.get $B:PROGRAMACOMPLETO
    global.set $Y:PROGRAMACOMPLETO:COMPARARYDEVOLVERMAY
    call $COMPARARYDEVOLVERMAY:PROGRAMACOMPLETO
    global.get $X:PROGRAMACOMPLETO:COMPARARYDEVOLVERMAY
    global.set $A:PROGRAMACOMPLETO
    global.get $Y:PROGRAMACOMPLETO:COMPARARYDEVOLVERMAY
    global.set $B:PROGRAMACOMPLETO
    i32.const 0
    global.set $flag_funcion_activa_COMPARARYDEVOLVERMAY:PROGRAMACOMPLETO
    global.set $RESULTADO:PROGRAMACOMPLETO
    global.get $RESULTADO:PROGRAMACOMPLETO
    call $print_i64
    i32.const 184
    i32.const 16
    call $print_string
    ;; Inicio de chequeo de llamada recursiva
    global.get $flag_funcion_activa_APLICAROPERACION:PROGRAMACOMPLETO
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
    global.set $flag_funcion_activa_APLICAROPERACION:PROGRAMACOMPLETO
    i64.const 15
    global.set $VALOR:PROGRAMACOMPLETO:APLICAROPERACION
    i64.const 0   ;; Indice en la tabla a guardar en parámetro formal
    global.set $OP:PROGRAMACOMPLETO:APLICAROPERACION
    call $APLICAROPERACION:PROGRAMACOMPLETO
    i32.const 0
    global.set $flag_funcion_activa_APLICAROPERACION:PROGRAMACOMPLETO
    global.set $RESULTADO:PROGRAMACOMPLETO
    global.get $RESULTADO:PROGRAMACOMPLETO
    call $print_i64
    i32.const 200
    i32.const 16
    call $print_string
    ;; Inicio de chequeo de llamada recursiva
    global.get $flag_funcion_activa_APLICAROPERACION:PROGRAMACOMPLETO
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
    global.set $flag_funcion_activa_APLICAROPERACION:PROGRAMACOMPLETO
    i64.const 7
    global.set $VALOR:PROGRAMACOMPLETO:APLICAROPERACION
    i64.const 1   ;; Indice en la tabla a guardar en parámetro formal
    global.set $OP:PROGRAMACOMPLETO:APLICAROPERACION
    call $APLICAROPERACION:PROGRAMACOMPLETO
    i32.const 0
    global.set $flag_funcion_activa_APLICAROPERACION:PROGRAMACOMPLETO
    global.set $RESULTADO:PROGRAMACOMPLETO
    global.get $RESULTADO:PROGRAMACOMPLETO
    call $print_i64
    i64.const 100
    global.set $A:PROGRAMACOMPLETO
    i64.const 200
    global.set $B:PROGRAMACOMPLETO
    i64.const 300
    global.set $C:PROGRAMACOMPLETO
    global.get $A:PROGRAMACOMPLETO
    call $print_i64
    global.get $B:PROGRAMACOMPLETO
    call $print_i64
    global.get $C:PROGRAMACOMPLETO
    call $print_i64
  )
  (data (i32.const 0) "Error: Overflow en la operación.\n")
  (data (i32.const 34) "Error: Perdida de precision al truncar la constante flotante.\n")
  (data (i32.const 96) "Error: No se puede llamar una función desde si misma.\n")
  (data (i32.const 151) "Factorial de 5:")
  (data (i32.const 166) "Mayor entre A y B:")
  (data (i32.const 184) "Lambda duplicar:")
  (data (i32.const 200) "Lambda cuadrado:")
)
