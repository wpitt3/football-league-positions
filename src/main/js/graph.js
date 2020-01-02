
function createGraph(teamNames, teamData) {

    if (document.getElementsByClassName("team_name").length === 0){
        createRows(teamNames)
    }
    addNamesToRows(teamNames)
    addDataToRows(teamData)
}

function createRows(teamsNames) {
    var chart = document.getElementById("chart");

    for (let i = 0; i < teamsNames.length; i++) {
        var row = document.createElement("div");
        row.classList.add("chart_row")
        var teamName = document.createElement("div")
        teamName.classList.add("team_name")
        row.appendChild(teamName);
        var dataRow = document.createElement("div")
        dataRow.classList.add("data_row")
        row.appendChild(dataRow);
        chart.appendChild(row);
    }
}

function addNamesToRows(teamsNames) {
    var teamsNameNodes = document.getElementsByClassName("team_name");
    for (let i = 0; i < teamsNameNodes.length; i++) {
        teamsNameNodes[i].textContent = (i+1).toString().padStart(2, '0') + ". " + teamsNames[i]
    }
}

function addDataToRows(teamData) {
    var teamsDataNodes = document.getElementsByClassName("data_row");

    for (let i = 0; i < teamData.length; i++) {
        while (teamsDataNodes[i].firstChild) {
            teamsDataNodes[i].removeChild(teamsDataNodes[i].firstChild);
        }

        var teamWeeks = formatTeamData(i+1, teamData);
        for (let j = 0; j < teamWeeks.length; j++) {
            if(teamsDataNodes[i].children.length < teamData.length) {
                var dataCell = document.createElement("div")
                dataCell.classList.add("data_cell")
                dataCell.classList.add("data_cell_"+teamWeeks[j])
                teamsDataNodes[i].appendChild(dataCell);
            }
        }
    }
}

function formatTeamData(currentIndex, teamData) {
    var upward = calculateWeekSize(currentIndex, teamData[currentIndex-1][0], 1)
    var downward = calculateWeekSize(currentIndex, teamData[currentIndex-1][1], teamData.length)
    var result = []
    for (let i = 0; i < upward.length; i++) {
        for (let j = 0; j < upward[i]; j++) {
            result.push(i+1);
        }
    }
    result = result.reverse()
    result.push(0)
    for (let i = 0; i < downward.length; i++) {
            for (let j = 0; j < downward[i]; j++) {
                result.push(i+1);
            }
        }
    return result
}

function calculateWeekSize(a, teamData, max) {
    var result = [];
    var currentIndex = a;
    for (let i = 0; i < teamData.length; i++) {
        result.push(Math.abs(currentIndex - teamData[i]))
        currentIndex = teamData[i];
    }
    result.push(Math.abs(currentIndex - max))
    return result
}