// ocultar contrasenia
function maskInput() {
    const input = document.getElementById("password")
    input.dataset.realPassword = input.value; // Guarda el valor real en un atributo de datos
    input.value = '*'.repeat(input.value.length); // Muestra asteriscos
    input.value = input.dataset.realPassword; // Restaura el valor real
    input.type = "password" // setear el tipo a password
}

maskInput()

// agregar nueva imagen
const inputImg = document.getElementById('input-img')
const perfilBtn = document.getElementById('perfil-btn')
const submitBtn = document.getElementById('perfil-submit-btn')
perfilBtn.addEventListener('click', () => {
    if(inputImg.type === 'hidden'){
        inputImg.type = 'file'
        submitBtn.style.display='block'
        perfilBtn.style.display='none'
    }
})