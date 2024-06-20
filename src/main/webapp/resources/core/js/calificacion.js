document.addEventListener("DOMContentLoaded", () => {
    let successMessage = document.getElementById("calificacion-success")
    let errorMessage = document.getElementById("calificacion-error")
    if(successMessage){
        successMessage.style.opacity = '1'
    }else if(errorMessage){
        errorMessage.style.opacity = '1'
    }

    setTimeout(() => {
        if(successMessage){
            successMessage.style.opacity = '0'
        }else if(errorMessage){
            errorMessage.style.opacity = '0'
        }
    }, 5000)
})