function sendit() {
    if ($('#user_id').val() === "") {
        alert("아이디는 필수 입력값입니다.");
        $('#user_id').focus();
        return false;
    }

    if ($('#login_pw').val() === "") {
        alert("비밀번호는 필수 입력값입니다.");
        $('#login_pw').focus();
        return false;
    }
    if ($('#login_pw_ok').val() === "") {
        alert("비밀번호확인은 필수 입력값입니다.");
        $('#login_pw_ok').focus();
        return false;
    }
    if ($('#user_name').val() === "") {
        alert("이름은 필수 입력값입니다.");
        $('#user_name').focus();
        return false;
    }

    let birth_year = $('#birth_year').val();
    let birth_month = $('#birth_month').val();
    let birth_day = $('#birth_day').val();
    const currentYear = new Date().getFullYear();

    if (birth_month < 10) {
        birth_month = '0' + birth_month;
    }

    if (birth_year.length !== 4 || birth_month.length !== 2 || birth_day.length !== 2) {
        alert("생년월일은 꼭 입력되야 합니다. (연: 0000, 월: 00, 일: 00)");
        return false;
    } else if (birth_year > currentYear || birth_month > 12 || birth_day > 31) {
        alert("생년월일을 올바르게 입력해주세요. (년도: 현재 년 이하, 월: 12 이하, 일: 31 이하)");
        return false;
    }

    let phoneRegEx = /^\d{3}\d{4}\d{4}$/;
    let isValidPhone = true;
    $('.phone_number').each(function () {
        if (!phoneRegEx.test($(this).val())) {
            isValidPhone = false;
            return false;
        }
    });


    let isempty1 = false;
    $('.emphone_number').each(function () {
        if ($(this).val() === "") {
            alert("긴급전화 항목은 필수 입력값입니다.");
            $(this).focus();
            isempty1 = true
            return false;
        }
    })
    if (isempty1) {
        return false;
    }

    if (!isValidEmergencyPhone) {
        alert("휴대전화 항목은 숫자 010-0000-0000 형식으로 입력되어야 합니다.");
        return false;
    }

    let isempty = false;
    $('.phone_number').each(function () {
        if ($(this).val() === "") {
            alert("휴대전화 항목은 필수 입력값입니다.");
            $(this).focus();
            isempty = true
            return false;
        }
    })
    if (isempty) {
        return false;
    }

    if (!isValidPhone) {
        alert("휴대전화 항목은 숫자 010-0000-0000 형식으로 입력되어야 합니다.");
        return false;
    }

    let isValidEmergencyPhone = true;
    $('.emphone_number').each(function () {
        if (!phoneRegEx.test($(this).val())) {
            isValidEmergencyPhone = false;
            return false;
        }
    });

    if ($('#store').val() === "") {
        alert("매장명 항목은 필수 입력값입니다.");
        $('#store').focus();
        return false;
    }

    if ($('#login_pw').val() != $('#login_pw_ok').val()) {
        alert("비밀번호 확인의 값이 다릅니다.");
        $('#login_pw_ok').val('');
        $('#login_pw_ok').focus();
        return false;
    }

    //정규식
    const exppw1 = /^(?=.*[a-zA-Z])(?=.*[0-9]).{10,16}$/; //영문, 숫자
    const exppw2 = /^(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9]).{10,16}$/; //영문, 특수문자
    const exppw3 = /^(?=.*[^a-zA-Z0-9])(?=.*[0-9]).{10,16}$/; //특수문자, 숫자
    const user_pw = $('#login_pw').val();

    if (!(exppw1.test(user_pw) || exppw2.test(user_pw) || exppw3.test(user_pw))) {
        alert("비밀번호 입력조건을 다시한번 확인해 주세요.")
        $('#login_pw').val('');
        $('#login_pw').focus();
        return false
    }

    if ($('#flag').val() === 'false') {
        alert('아이디 중복확인을 해주세요')
        return false
    }
    if ($('#birth_year').val() > 2023) {
        alert('생년을 다시 확인 해주세요')
        return false;
    }

}

$(function () {
    //로그인 되어있는 경우면 인덱스로 튕겨내기
    if ($('#loginTrue').val() === 'loginTrue') {
        location.href = '/main/index';
    }

    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");
    //아이디 중복확인 ajax
    $('#double_check').on('click', function () {
        $.ajax({
            type: 'POST',
            url: '/main/register/doublecheck',
            data: {registerId: $('#user_id').val()},
            beforeSend: function (xhr) {   /*데이터를 전송하기 전에 헤더에 csrf값을 설정한다*/
                xhr.setRequestHeader(header, token);
            }
        }).done(function (word) {
            $('#flag').val('true');
            alert(word);
        }).fail(function (error) {
            alert(JSON.stringify(error));
        })

    })
    $('#user_id').on('change', function () {
        $('#flag').val('false');
    })

    $(".phone_number, .emphone_number").on("input", function () {
        let numbersOnlyValue = $(this).val().replace(/\D/g, "").substring(0, 11);
        let formattedValue = numbersOnlyValue.replace(/^(\d{3})?(\d{4})?(\d{4})?$/, function (_, g1, g2, g3) {
            return (g1 ? g1 + "" : "") + (g2 ? g2 + "" : "") + (g3 || "");
        });
        $(this).val(formattedValue);
    });
})