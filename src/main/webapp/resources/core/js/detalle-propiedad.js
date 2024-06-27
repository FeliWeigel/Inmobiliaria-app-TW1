document.addEventListener("DOMContentLoaded", () => {
    let imagenPrincipal = document.getElementById('img-principal');
    window.seleccionarImagen = function (imagen) {
        imagenPrincipal.src = imagen.src;
    };
})