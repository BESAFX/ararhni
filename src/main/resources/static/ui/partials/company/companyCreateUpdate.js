app.controller('companyCreateUpdateCtrl', ['CompanyService', 'PersonService', 'FileUploader', 'FileService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'company',
    function (CompanyService, PersonService, FileUploader, FileService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, company) {

        $timeout(function () {
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
            switch ($scope.action) {
                case 'create' :
                    CompanyService.create($scope.company).then(function (data) {
                        $scope.company = {};
                        $scope.form.$setPristine();
                    });
                    break;
                case 'update' :
                    CompanyService.update($scope.company).then(function (data) {
                        $scope.company = data;
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $scope.fetchPersonData = function () {
            PersonService.findAllSummery().then(function (data) {
                $scope.persons = data;
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