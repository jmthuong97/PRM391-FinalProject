const HttpStatus = require('http-status-codes');

module.exports = class HttpResponse {
    static ok(response) {
        return {
            statusCode: HttpStatus.OK,
            body: {
                status: HttpStatus.OK,
                data: response,
            }
        }
    }

    static serverError(error) {
        return {
            statusCode: HttpStatus.INTERNAL_SERVER_ERROR,
            body: {
                status: HttpStatus.INTERNAL_SERVER_ERROR,
                message: error.message,
            },
        };
    }

    static unauthorizedError(error) {
        return {
            statusCode: HttpStatus.UNAUTHORIZED,
            body: {
                status: HttpStatus.UNAUTHORIZED,
                message: error.message,
            },
        };
    }

    static notFoundError(error) {
        return {
            statusCode: HttpStatus.NOT_FOUND,
            body: {
                status: HttpStatus.NOT_FOUND,
                message: error.message,
            },
        };
    }

    static badRequestError(error) {
        return {
            status: HttpStatus.BAD_REQUEST,
            body: {
                status: HttpStatus.BAD_REQUEST,
                message: error.message
            },
        };
    }
};
