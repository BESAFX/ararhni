app.controller('employeeCreateUpdateCtrl', ['EmployeeService', 'DepartmentService', 'PersonService', 'FileService', 'FileUploader', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'employee',
    function (EmployeeService, DepartmentService, PersonService, FileService, FileUploader, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, employee) {

        $scope.fetchDepartmentData = function () {
            DepartmentService.fetchTableData().then(function (data) {
                $scope.departments = data
                $rootScope.showToast("تم تحميل بيانات الأقسام بنجاح");
            });
        };

        $scope.fetchPersonData = function () {
            PersonService.findAll().then(function (data) {
                $scope.persons = data;
                $rootScope.showToast("تم تحميل بيانات المستخدمين بنجاح");
            });
        };

        $timeout(function () {
            $rootScope.showToast("جاري تحميل بيانات الأقسام والمستخدمين، فضلاَ انتظر قليلاً");
            $scope.fetchDepartmentData();
            $scope.fetchPersonData();
        }, 1500);

        if (employee) {
            $scope.employee = employee;
        } else {
            $scope.employee = {};
        }

        $scope.title = title;

        $scope.action = action;

        $scope.submit = function () {
            $rootScope.showToast("جاري القيام بالعملية، فضلاً انتظر قليلاً");
            switch ($scope.action) {
                case 'create' :
                    EmployeeService.create($scope.employee).then(function (data) {
                        $scope.employee = {};
                        $scope.form.$setPristine();
                        $rootScope.showToast("تم القيام بالعملية بنجاح، يمكنك اضافة موظف آخر الآن");
                    });
                    break;
                case 'update' :
                    EmployeeService.update($scope.employee).then(function (data) {
                        $scope.employee = data;
                        $rootScope.showToast("تم القيام بالعملية بنجاح، يمكنك متابعة عملك الآن");
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        var uploader = $scope.uploader = new FileUploader({
            url: 'uploadFile'
        });

        uploader.filters.push({
            name: 'syncFilter',
            fn: function (item, options) {
                return this.queue.length < 10;
            }
        });

        uploader.filters.push({
            name: 'asyncFilter',
            fn: function (item, options, deferred) {
                setTimeout(deferred.resolve, 1e3);
            }
        });

        uploader.onAfterAddingFile = function (fileItem) {
            uploader.uploadAll();
        };
        uploader.onSuccessItem = function (fileItem, response, status, headers) {
            $scope.employee.person.photo = response;
            FileService.getSharedLink(response).then(function (data) {
                $scope.logoLink = data;
            });
        };

    }]);