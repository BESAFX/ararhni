app.controller('incomingTasksDeductionsCtrl', ['$scope', '$rootScope', '$timeout', '$uibModalInstance', 'PersonService', '$http',
    function ($scope, $rootScope, $timeout, $uibModalInstance, PersonService, $http) {

        $scope.buffer = {};

        $scope.personList = [];

        $scope.submit = function () {
            var listId = [];
            for (var i = 0; i < $scope.buffer.personList.length; i++) {
                listId[i] = $scope.buffer.personList[i].id;
            }

            var search = [];

            search.push('personList=');
            search.push(listId);
            search.push('&');

            if ($scope.buffer.closeType) {
                search.push('closeType=');
                search.push($scope.buffer.closeType);
                search.push('&');
            }
            if ($scope.buffer.startDate) {
                search.push('startDate=');
                search.push($scope.buffer.startDate.getTime());
                search.push('&');
            }
            if ($scope.buffer.endDate) {
                search.push('endDate=');
                search.push($scope.buffer.endDate.getTime());
                search.push('&');
            }

            switch ($scope.action) {
                case 'view':
                    window.open('/report/IncomingTasksDeductions?' + search.join(""));
                    break;
                case 'download':
                    $http.get('/report/IncomingTasksDeductions?' + search.join(""), {responseType: 'arraybuffer'}).then(function (response) {
                        var blob = new Blob([response.data], {type: 'application/pdf'});
                        saveAs(blob, 'Report.pdf');
                    });
                    break;
                case 'send':
                    if ($scope.buffer.email) {
                        search.push('email=');
                        search.push($scope.buffer.email);
                        search.push('&');
                    }
                    if ($scope.buffer.title) {
                        search.push('title=');
                        search.push($scope.buffer.title);
                        search.push('&');
                    }
                    if ($scope.buffer.message) {
                        search.push('message=');
                        search.push($scope.buffer.message);
                        search.push('&');
                    }
                    $rootScope.showNotify("المهام", "فضلاً انتظر قليلا حتى يتم اعداد التقرير وارساله الى البريد المدخل...", "warning", "fa-black-tie");
                    $http.get('/report/email/IncomingTasksDeductions?' + search.join("")).then(function (response) {
                        $rootScope.showNotify("المهام", "تم ارسال المهمة بنجاح", "success", "fa-black-tie");
                    });
                    break;
            }
        };


        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            PersonService.findPersonUnderMeSummery().then(function (data) {
                $scope.persons = data;
            })
        }, 1500);

    }]);