// const mp = new MercadoPago("APP_USR-ce16a581-25f1-479c-b2bb-db5ad1368dc9", {
//     locale: "es-AR", // The most common are: 'pt-BR', 'es-AR' and 'en-US'
// });

document.addEventListener("DOMContentLoaded", async () => {
    try {

        const response = await fetch('http://localhost:8080/spring/create_preference',{
            method:"POST",
            // headers:{"Content-Type":"application/json"},
            // body: JSON.stringify({"some_data": sendData
        })

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        const preference = await response.json();
        const preferenceId = preference.id;

        console.log(response);

        const mp = new MercadoPago("APP_USR-ce16a581-25f1-479c-b2bb-db5ad1368dc9", {
            locale: "es-AR"
        });
        const bricksBuilder = mp.bricks();

        bricksBuilder.create("wallet", "wallet_container", {
            initialization: {
                preferenceId: preferenceId,
            },
            customization: {
                texts: {
                    valueProp: 'smart_option',
                },
            },
        });
    } catch (error) {
        console.error('Error fetching preference ID:', error);
    }
})