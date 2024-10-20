import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRefund } from '../refund.model';
import { RefundService } from '../service/refund.service';

const refundResolve = (route: ActivatedRouteSnapshot): Observable<null | IRefund> => {
  const id = route.params['id'];
  if (id) {
    return inject(RefundService)
      .find(id)
      .pipe(
        mergeMap((refund: HttpResponse<IRefund>) => {
          if (refund.body) {
            return of(refund.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default refundResolve;
