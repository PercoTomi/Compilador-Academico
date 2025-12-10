const fs = require('fs');

// 1. Leemos el binario .wasm compilado
const wasmBuffer = fs.readFileSync('./ejecutable.wasm');

// 2. Preparamos la memoria (Debe coincidir con (memory 1) de tu wat)
// Tu wat pide importar "env" "mem", así que la creamos aquí.
const memory = new WebAssembly.Memory({ initial: 1 });

// 3. Definimos las funciones que el WASM va a importar
const importObject = {
      env: {
            // A. Pasamos la memoria creada
            mem: memory,

            // B. Función para imprimir números (i64 e i32)
            // Nota: JS recibe BigInt para i64, lo convertimos a string para verlo bien
            print_i64: (valor) => {
                  console.log("[WASM Output Num]:", valor.toString());
            },

            print_i32: (valor) => {
                  console.log("[WASM Output Num]:", valor);
            },
            // C. Función para imprimir Strings desde la memoria
            print_string: (offset, length) => {
                  const buffer = new Uint8Array(memory.buffer);
                  // Extraemos los bytes exactos
                  const bytes = buffer.subarray(offset, offset + length);
                  // Decodificamos a texto
                  const texto = new TextDecoder("utf-8").decode(bytes);
                  console.log("[WASM Output Str]:", texto);
            }
      }
};

// 4. Instanciamos y ejecutamos
WebAssembly.instantiate(wasmBuffer, importObject)
      .then(wasmModule => {
            console.log("--- Iniciando Ejecución ---");
            try {
                  // Ejecutamos la función main exportada
                  wasmModule.instance.exports.main();
                  console.log("--- Ejecución Finalizada con Éxito ---");
            } catch (e) {
                  console.error("--- Error de Ejecución (Trap) ---");
                  console.error(e);
            }
      })
      .catch(err => {
            console.error("Error al instanciar el módulo:", err);
      });