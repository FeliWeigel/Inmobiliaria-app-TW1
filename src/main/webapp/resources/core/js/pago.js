const mp = new MercadoPago("APP_USR-ce16a581-25f1-479c-b2bb-db5ad1368dc9", {
    locale: "es-AR", // The most common are: 'pt-BR', 'es-AR' and 'en-US'
});

const bricksBuilder = mp.bricks();

mp.bricks().create("wallet", "wallet_container", {
    initialization: {
        preferenceId: "<PREFERENCE_ID>",
    },
    customization: {
        texts: {
            valueProp: 'smart_option',
        },
    },
});