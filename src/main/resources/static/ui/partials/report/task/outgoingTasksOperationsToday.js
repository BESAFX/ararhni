app.controller('outgoingTasksOperationsTodayCtrl', ['$scope', '$rootScope', '$timeout', '$uibModalInstance', 'PersonService',
    function ($scope, $rootScope, $timeout, $uibModalInstance, PersonService) {

        $scope.buffer = {};

        $scope.submit = function () {
            var search = [];
            search.push('personId=');
            search.push($scope.buffer.person.id);
            search.push('&');
            search.push('title=');
            search.push($scope.buffer.title);
            search.push('&');
            window.open('/report/ReportOutgoingTasksOperationsToday?' + search.join(""));
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