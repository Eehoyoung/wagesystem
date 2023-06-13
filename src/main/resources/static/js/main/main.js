$(document).ready(function() {
    alert("00")
    $("#checkInBtn").click(function() {
        const employeeId = $("#employeeId").val();
        const checkInTime = new Date().toISOString();

        // 출근 시간 기록 및 서버에 전송하는 기능 구현
        $.ajax({
            url: "/start-work",
            type: "POST",
            data: { employeeId, checkInTime },
            success: function(response) {
                console.log(response); // 출근 처리 성공 메시지 출력
            },
            error: function(err) {
                console.error(err); // 에러 메시지 출력
            }
        });
    });

    $("#checkOutBtn").click(function() {
        const employeeId = $("#employeeId").val();
        const checkOutTime = new Date().toISOString();

        // 퇴근 시간 기록 및 서버에 전송하는 기능 구현
        $.ajax({
            url: "/end-work",
            type: "POST",
            data: { employeeId, checkOutTime },
            success: function(response) {
                console.log(response); // 퇴근 처리 성공 메시지 출력
            },
            error: function(err) {
                console.error(err); // 에러 메시지 출력
            }
        });
    });
});

