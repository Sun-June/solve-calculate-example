
function fetchUrl(url, data) {
    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
    };
    return fetch(url, requestOptions);
}

function numberCalculation(data) {
    return fetchUrl('../number/calculation', data);
}

function mixedCalculation(data) {
    return fetchUrl('../mixed/calculation', data);
}

function checkFormula(data) {
    return fetchUrl('../mixed/check', data);
}

function renderTable(id, result, height) {
    layui.use('table', function(){
        const table = layui.table;
        const recordList  = result.recordList;

        const list = []
        for (const record of recordList) {
            if (record.kind != "LITERAL" || record.arithmetic.includes("number") || record.arithmetic.includes("str")) {
                let process = record.arithmetic;
                if (record.kind == "LITERAL") {
                    process = `${record.arithmetic} = ${record.result}`
                } else if (record.kind == "FUNCTION") {
                    process = `${record.arithmetic}(`
                    record.values.forEach((value, index) => {
                        if (index === 0) {
                            process += value
                        } else {
                            process += `, ${value}`
                        }
                    })
                    process += `) = ${record.result}`
                } else if (record.kind == "OPERATOR") {
                    process = `${record.values[0]} ${record.arithmetic} ${record.values[1]} = ${record.result}`
                } else if (record.kind == "MONADIC_OPERATOR") {
                    if (record.arithmetic.includes("-")) {
                        process = `-${record.values[0]} = ${record.result}`
                    } else {
                        process = `${record.values[0]}${record.arithmetic} = ${record.result}`
                    }
                }
                list.push({arithmetic: record.arithmetic, index: record.index, kind: record.kind, process})
            }
        }

        const inst = table.render({
            elem: `#${id}`,
            cols: [[ //标题栏
                {field: 'index', title: 'formulaIndex', width: 150},
                {field: 'arithmetic', title: 'arithmetic', width: 150},
                {field: 'kind', title: 'kind', width: 300},
                {field: 'process', title: 'process', minWidth: 280, totalRowText: `final result：${result.result}`}
            ]],
            data: list,
            page: false, // 是否显示分页
            height,
            totalRow: true
        });
    });
}
