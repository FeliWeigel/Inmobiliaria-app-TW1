"use strict";
document.addEventListener("DOMContentLoaded", () => {
    // agregar favoritos propiedad
    let successMessage = document.getElementById("errorMessage")
    if(successMessage){
        successMessage.style.opacity = '1'
    }
    setTimeout(() => {
        if(successMessage){
            successMessage.style.opacity = '0'
        }
    }, 10000)
})

