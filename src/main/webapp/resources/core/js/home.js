document.addEventListener("DOMContentLoaded", function() {
    // Seleccionar el botón de favorito por su id
    var favoriteButtons = document.querySelectorAll('.favHeart')
    favoriteButtons.forEach((favoriteButton) => {
        if (favoriteButton) {
            // Agregar un event listener para el clic en el botón de favorito
            favoriteButton.addEventListener('click', function(event) {
                event.preventDefault();
                var favorite = this.dataset.favorite;

                // Realizar una solicitud POST a '/favorite'
                var xhr = new XMLHttpRequest();
                xhr.open('POST', '/spring/favorite', true);
                xhr.setRequestHeader('Content-Type', 'application/json');
                xhr.onreadystatechange = function() {
                    if (xhr.readyState === 4) {
                        if (xhr.status === 200) {
                            // Manejar la respuesta exitosa
                            console.log('Added to favorites');
                        } else {
                            // Manejar la respuesta de error
                            console.error('Failed to add to favorites');
                        }
                    }
                };
                xhr.send(JSON.stringify({ favorite: favorite }));
            });
        }
    })



    // Seleccionar el botón de deshacer favorito por su id

    var unfavoriteButtons = document.querySelectorAll('.unfavHeart');
    unfavoriteButtons.forEach((unfavoriteButton) => {
        if (unfavoriteButton) {
            // Agregar un event listener para el clic en el botón de deshacer favorito
            unfavoriteButton.addEventListener('click', function(event) {
                event.preventDefault();
                var favorite = this.dataset.favorite;

                // Realizar una solicitud POST a '/unfavorite'
                var xhr = new XMLHttpRequest();
                xhr.open('POST', '/spring/unfavorite', true);
                xhr.setRequestHeader('Content-Type', 'application/json');
                xhr.onreadystatechange = function() {
                    if (xhr.readyState === 4) {
                        if (xhr.status === 200) {
                            // Manejar la respuesta exitosa
                            console.log('Removed from favorites');
                        } else {
                            // Manejar la respuesta de error
                            console.error('Failed to remove from favorites');
                        }
                    }
                };
                xhr.send(JSON.stringify({ favorite: favorite }));
            });
        }
    })

});
