(module
  (import "env" "mem" (memory 1))
  (import "env" "print_i64" (func $print_i64 (param i64)))
  (import "env" "print_string" (func $print_string (param i32 i32)))
  (global $aux_overflow (mut i64) (i64.const 0))
  (global $aux_float (mut f64) (f64.const 0.0))
  (global $X:P (mut i64) (i64.const 0))
  (func $main (export "main") 
    (block $salidaDoUntil1
      (loop $loopDoUntil1
      global.get $X:P
      i64.const 1
      i64.add
      global.set $aux_overflow
      global.get $aux_overflow
      i64.const 2147483647
      i64.gt_s
      (if
        (then
          i32.const 0
          i32.const 25
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
          i32.const 25
          call $print_string
          unreachable
        )
      )
      global.get $aux_overflow
      global.set $X:P
      global.get $X:P
      call $print_i64
      global.get $X:P
      i64.const 10
      i64.le_s
      br_if $loopDoUntil1
      br $salidaDoUntil1
      )
    )
  )
  (data (i32.const 0) "Error: Overflow en suma.\n")
  (data (i32.const 25) "Error: Perdida de precision al truncar la constante flotante.\n")
  (data (i32.const 87) "Error: No se puede llamar una función desde si misma.\n")
)
