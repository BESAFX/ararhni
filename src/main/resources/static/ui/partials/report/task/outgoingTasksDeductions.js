app.controller('outgoingTasksDeductionsCtrl', ['$scope', '$rootScope', '$timeout', '$uibModalInstance', 'PersonService',
    function ($scope, $rootScope, $timeout, $uibModalInstance, PersonService) {

        $scope.buffer = {};

        $scope.submit = function () {

            var search = [];

            search.push('personId=');
            search.push($scope.buffer.person.id);
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

            window.open('/report/OutgoingTasksDeductions?' + search.join(""));
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