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

/*function manejadorFavorito(){
    let iconoVacio = document.getElementById('fav-vacio')
    let iconoRelleno = document.getElementById('fav-relleno')

    if(iconoVacio.style.display !== 'none'){
        iconoVacio.style.display = 'none'
        iconoRelleno.style.display = 'block'
    }else {
        iconoRelleno.style.display = 'none'
        iconoVacio.style.display = 'block'
    }
}*/
