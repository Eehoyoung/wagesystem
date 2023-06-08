async function startWork() {
    const employeeIdInput = document.getElementById("employee-id-input");
    const employeeId = employeeIdInput.value;
    const response = await fetch("http://localhost:8080/start-work?employeeId=" + employeeId, {method: "POST"});
    const data = await response.json();
    console.log(data);
    alert("출근처리 되었습니다. 오늘도 화이팅!!")
}

async function endWork() {
    const employeeIdInput = document.getElementById("employee-id-input");
    const employeeId = employeeIdInput.value;
    const response = await fetch("/end-work?employeeId=" + employeeId, {method: "POST"});
    const data = await response.json();
    console.log(data);
    alert("퇴근처리 되었습니다. 수고하셨습니다!!")
}