app.controller('companyCreateUpdateCtrl', ['CompanyService', 'PersonService', 'FileUploader', 'FileService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'company',
    function (CompanyService, PersonService, FileUploader, FileService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, company) {

        $timeout(function () {
            $rootScope.showNotify("الشركات", "جاري تحميل بيانات المستخدمين، فضلاَ انتظر قليلاً", "warning", "fa-bank");
            $scope.fetchPersonData();
        }, 1500);

        if (company) {
            $scope.company = company;
            if (company.logo) {
                FileService.getSharedLink(company.logo).then(function (data) {
                    $scope.logoLink = data;
                });
            }
        } else {
            $scope.company = {};
        }

        $scope.title = title;

        $scope.action = action;

        $scope.submit = function () {
            $rootScope.showNotify("الشركات", "جاري القيام بالعملية، فضلاً انتظر قليلاً", "warning", "fa-bank");
            switch ($scope.action) {
                case 'create' :
                    CompanyService.create($scope.company).then(function (data) {
                        $scope.company = {};
                        $scope.form.$setPristine();
                        $rootScope.showNotify("الشركات", "تم القيام بالعملية بنجاح، يمكنك اضافة شركة آخرى الآن", "success", "fa-bank");
                    });
                    break;
                case 'update' :
                    CompanyService.update($scope.company).then(function (data) {
                        $scope.company = data;
                        $rootScope.showNotify("الشركات", "تم القيام بالعملية بنجاح، يمكنك متابعة عملك الآن", "success", "fa-bank");
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $scope.fetchPersonData = function () {
            PersonService.findAll().then(function (data) {
                $scope.persons = data;
                $rootScope.showNotify("الشركات", "تم تحميل بيانات المستخدمين بنجاح", "success", "fa-bank");
            })
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
            $scope.company.logo = response;
            FileService.getSharedLink(response).then(function (data) {
                $scope.logoLink = data;
            });
        };

    }]);