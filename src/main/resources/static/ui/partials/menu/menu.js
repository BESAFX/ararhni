app.controller("menuCtrl", ['$scope', '$rootScope', '$state', '$timeout', function ($scope, $rootScope, $state, $timeout) {

    $scope.goToCompany = function () {
        $state.go('company');
    };
    $scope.goToRegion = function () {
        $state.go('region');
    };
    $scope.goToBranch = function () {
        $state.go('branch');
    };
    $scope.goToDepartment = function () {
        $state.go('department');
    };
    $scope.goToEmployee = function () {
        $state.go('employee');
    };
    $scope.goToTeam = function () {
        $state.go('team');
    };
    $scope.goToPerson = function () {
        $state.go('person');
    };
    $scope.goToReportModel = function () {
        $state.go('reportModel');
    };
    $scope.goToTask = function () {
        $state.go('task');
    };
    $scope.goToHome = function () {
        $state.go('home');
    };
    $timeout(function () {
        window.componentHandler.upgradeAllRegistered();
    }, 1500);
}]);