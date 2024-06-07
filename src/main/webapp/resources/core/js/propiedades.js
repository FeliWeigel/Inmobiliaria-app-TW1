"use strict";
document.addEventListener("DOMContentLoaded", () => {
    let successMessage = document.getElementById("successMessage")
    if(successMessage){
        successMessage.style.opacity = '1'
    }
    setTimeout(() => {
        if(successMessage){
            successMessage.style.opacity = '0'
        }
    }, 5000)
})

const inputAlquiler = document.getElementById('input-alquiler')
const inputVenta = document.getElementById('input-venta')
const inputPrecio = document.getElementById('precio')
const actualizarPlaceholder = () => {
    if(inputAlquiler.checked){
        inputPrecio.setAttribute('placeholder', '$(ARS)')
    }else if(inputVenta.checked){
        inputPrecio.setAttribute('placeholder', '$(USD)')
    }
}
inputAlquiler.addEventListener("change", actualizarPlaceholder)
inputVenta.addEventListener("change", actualizarPlaceholder)
window.addEventListener("load", () => {
    inputAlquiler.checked = true
    actualizarPlaceholder()
})
