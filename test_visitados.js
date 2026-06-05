const axios = require('axios');
axios.post('https://mercader-server.onrender.com/api/juegos/sistema/visitados', {
    allGames: true, order: "desc"
}, {
    headers: { 'Authorization': 'Bearer test' }
}).catch(err => {
    // Actually the backend endpoint is GET.
    // I can't do GET with body easily with axios.
});
