import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(private toastr: ToastrService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(catchError(this.catchErrors()));
  }

  private catchErrors() {
    return (response: HttpErrorResponse) => {
      console.log('Inside interceptor');
      switch (response.status) {
        case 400:
          this.toastError(response.error['error'], 'Parse Error');
          break;
        case 500:
          this.toastError(response.error['error'], 'IO Error');
          break;

        // default case for other status type
        default:
          break;
      }

      return throwError(response);
    };
  }

  toastError(message: string, title: string) {
    this.toastr.error(message, title);
  }
}
