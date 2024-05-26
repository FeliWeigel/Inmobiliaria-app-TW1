let isMasked = true;

function maskInput() {
    const input = document.getElementById('password');
    console.log(input)
    if (isMasked) {
        const value = input.value;
        input.dataset.value = value; // Almacena el valor real en un atributo de datos
        input.value = '*'.repeat(value.length); // Muestra asteriscos
    }
}

maskInput()