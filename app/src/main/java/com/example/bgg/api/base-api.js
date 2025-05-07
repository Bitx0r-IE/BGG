const API_URL = "https://api.geekdo.com/xmlapi2";
const parser = new DOMParser();

async function start() {
    const query = prompt("Enter board game title or BGG ID:");
    if (!query) {
        alert("No query entered.");
        return;
    }

    let gameData;
    try {
        if (/^\d+$/.test(query)) {
            gameData = await getByBGGId(query);
        } else {
            const results = await getByQuery(query);
            const options = results.map((r, i) => `${i + 1}. ${r.name}`).join("\n");
            const choice = prompt(`Choose a game:\n${options}`);
            const index = parseInt(choice) - 1;
            if (isNaN(index) || index < 0 || index >= results.length) {
                alert("Invalid choice.");
                return;
            }
            gameData = await getByBGGId(results[index].id);
        }

        displayGameData(gameData);
    } catch (err) {
        console.error(err);
        alert("Something went wrong. See console for details.");
    }
}

async function getByQuery(query) {
    const xml = await apiGet(`${API_URL}/search`, { query, type: "boardgame" });
    const xmlDoc = parser.parseFromString(xml, "text/xml");
    const items = xmlDoc.getElementsByTagName("item");

    const results = [];
    for (let i = 0; i < items.length; i++) {
        results.push({
            id: items[i].getAttribute("id"),
            name: items[i].getElementsByTagName("name")[0].getAttribute("value"),
        });
    }
    return results;
}

async function getByBGGId(id) {
    const xml = await apiGet(`${API_URL}/thing`, { id });
    const xmlDoc = parser.parseFromString(xml, "text/xml");
    const item = xmlDoc.getElementsByTagName("item")[0];

    return {
        id: item.getAttribute("id"),
        name: item.getElementsByTagName("name")[0].getAttribute("value"),
        thumbnail: item.getElementsByTagName("image")[0]?.textContent || "",
        description: item.getElementsByTagName("description")[0]?.textContent || "",
    };
}

async function apiGet(url, params) {
    const fullUrl = new URL(url);
    Object.entries(params).forEach(([key, val]) => fullUrl.searchParams.append(key, val));
    const res = await fetch(fullUrl.href);
    return res.text();
}

function displayGameData(game) {
    const container = document.querySelector(".container");
    container.innerHTML += `
        <div style="margin-top: 2em;">
            <h2>${game.name}</h2>
            <img src="${game.thumbnail}" alt="${game.name}" width="200">
            <p>${game.description}</p>
        </div>
    `;
}
