
function createGraph(teamData, colours) { 
	  google.charts.load('current', {'packages':['corechart']});
      google.charts.setOnLoadCallback(drawChart);


      function drawChart() {
      	var weeks = teamData[0][0].length
        var data = new google.visualization.DataTable();
        data.addColumn('number', 'x');
        data.addColumn('number', 'values');
		for (let step = weeks-1; step >= 0; step--) {
 			data.addColumn({id:'i'+step, type:'number', role:'interval'});

        }
        for (let step = 0; step < weeks; step++) {
 			data.addColumn({id:'i'+step, type:'number', role:'interval'});
        }

        rows = []
 		for (let step = 0; step < teamData.length; step++) {
            let y = 50
            for (let x = 0; x < y; x++) {
    			rows[step*y + x] = ([step*y+x+1,step+1].concat(teamData[step][0].reverse())).concat(teamData[step][1])
                teamData[step][0].reverse()
            }
        
 		}

        data.addRows(rows);

        intervals = {}
        for (let step = 0; step < weeks; step++) {
			intervals['i'+step] = { 'style':'area', 'fillOpacity':1.0, 'color':colours[step]}
 		}

        var options_lines = {
            title: 'Prem',
            curveType: 'none',
            lineWidth: 2,
            height: 800,
            orientation: 'vertical',
            vAxis: { direction: -1, gridlines:{count:10}, baseline: 0},
           	hAxis: { gridlines:{count:10}, baseline: 1 },
            interval: intervals,
            legend: 'none',
        };

        var chart_lines = new google.visualization.LineChart(document.getElementById('chart_lines'));
        chart_lines.draw(data, options_lines);
      }
}