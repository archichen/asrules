var myChart;

$(document).ready(function() {
    // 基于准备好的dom，初始化echarts实例
    myChart = echarts.init(document.getElementById('main'));
});

function gpaorscore() {
    let gos = $('#gpaorscore').val();
    if (gos === 'score') {
        $('#antecedent').attr({'type': 'text', 'placeholder': '数学:A,英语:B,语文:C...'});
    } else {
        $('#antecedent').attr({'type': 'number', 'placeholder': '1.0'});
    }
}

function request() {
    let gos = $('#gpaorscore').val();
    let antecedent = $('#antecedent').val();
    if (gos === 'GPA') {
        antecedent = parseFloat(antecedent);
        if (antecedent >= 0 && antecedent < 2) {
            antecedent = 'C';
        } else if (antecedent >= 2 && antecedent < 3) {
            antecedent = 'B';
        } else if (antecedent >= 3) {
            antecedent = 'A';
        }
    }
    let schoolarship = $('#schoolarship').val();
    let competition = $('#competition').val();
    let similarityratio = $('#similarityratio').val();
    let consequenttype = $('#consequenttype').val();
    let consequent = $('#consequent').val();
    let rule = `${consequenttype}_${gos}`;
    let antecedentInput = `${antecedent},${schoolarship},${competition}`;
    antecedentInput = antecedentInput.replace(',,', ',');
    antecedentInput = antecedentInput.replace(',\n', '\n');
    console.log(antecedentInput);
    console.log(consequent);
    console.log(similarityratio);
    console.log(rule);
    $.post(
        '/getAsRules',
        {
            antecedentInput: antecedentInput,
            consequentInput: consequent,
            similarityRatio: similarityratio,
            type: rule
        },
        function(data, status) {
            setAdvise(data);
        }
    );
    // let consequent = $()
}

function kyorrd() {
    let ct = $('#consequenttype').val(); 
    let consequent = $('#consequent');
    if (ct === 'KY') {
        consequent.empty();
        consequent.append(`<option value='名校'>名校</option>`);
        consequent.append(`<option value='普通院校'>普通院校</option>`);
    } else {
        consequent.empty();
        consequent.append(`<option value='党员'>党员</option>`);
    }
}

function setAdvise(data) {
    let x = [];
    let y = [];
    let antecedents = [];
    data.forEach(rule => {
        x.push(rule.consequent);
        y.push(parseFloat(rule.confidence).toFixed(2));
        antecedents.push(rule.antecedent);
    });

    // 指定图表的配置项和数据
    var option = {
        title: {
            text: `${$('#consequenttype').val() === 'KY' ? '考研' + '到' + $('#consequent').val() : '入党'}可能性分析与建议`,
            textStyle: {
                color: '#f5f5f5'
            }
        },
        tooltip: {},
        legend: {
            data:['销量']
        },
        textStyle: {
            color: '#f5f5f5'
        },
        xAxis: {
            data: x
        },
        yAxis: {
            max: 1
        },
        series: [{
            name: '置信度',
            type: 'bar',
            data: y
        }]
    };

    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);

    myChart.on('click', function (params) {
        let adviseArr = antecedents[params.dataIndex];
        $('#advise').empty();
        for (let i in adviseArr) {
            let adviseStr = adviseArr[i];
            if (adviseStr.indexOf("A") >= 0 || adviseStr.indexOf("B") >= 0 || adviseStr.indexOf("C") >= 0) {
                if ($('#gpaorscore').val() === 'GPA') {
                    adviseStr = "提升绩点至：" + adviseStr;
                } else {
                    adviseStr = "提升成绩至：" + adviseStr;
                }
            } else {
                adviseStr = "获得：" + adviseStr;
            }
            $('#advise').append(`<li class="list-group-item">${adviseStr}</li>`);
        }
    });
}