app.controller('incomingTasksDeductionsCtrl', ['$scope', '$rootScope', '$timeout', '$uibModalInstance', 'PersonService',
    function ($scope, $rootScope, $timeout, $uibModalInstance, PersonService) {

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
                search.push($scope.buffer.startDate);
                search.push('&');
            }
            if ($scope.buffer.endDate) {
                search.push('endDate=');
                search.push($scope.buffer.endDate);
                search.push('&');
            }

            window.open('/report/IncomingTasksDeductions?' + search.join(""));
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