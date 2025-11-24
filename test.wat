(module
  (global $X:P (mut i64) (i64.const 0))
  (func $main (export "main") 
    (block $else1
      (block $then1
        global.get $X:P
        i64.const 3
        i64.le_s
        br_if $then1
        i64.const 4
        global.set $X:P
        br $else1
      )
      i64.const 5
      global.set $X:P
      (block $else2
        (block $then2
          global.get $X:P
          i64.const 4
          i64.le_s
          br_if $then2
          i64.const 7
          global.set $X:P
          br $else2
        )
        i64.const 8
        global.set $X:P
      )
    )
  )
)
