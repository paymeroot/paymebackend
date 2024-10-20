import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IKyc } from '../kyc.model';
import { KycService } from '../service/kyc.service';

const kycResolve = (route: ActivatedRouteSnapshot): Observable<null | IKyc> => {
  const id = route.params['id'];
  if (id) {
    return inject(KycService)
      .find(id)
      .pipe(
        mergeMap((kyc: HttpResponse<IKyc>) => {
          if (kyc.body) {
            return of(kyc.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default kycResolve;
