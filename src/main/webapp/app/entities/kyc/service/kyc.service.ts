import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IKyc, NewKyc } from '../kyc.model';

export type PartialUpdateKyc = Partial<IKyc> & Pick<IKyc, 'id'>;

export type EntityResponseType = HttpResponse<IKyc>;
export type EntityArrayResponseType = HttpResponse<IKyc[]>;

@Injectable({ providedIn: 'root' })
export class KycService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/kycs');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/kycs/_search');

  create(kyc: NewKyc): Observable<EntityResponseType> {
    return this.http.post<IKyc>(this.resourceUrl, kyc, { observe: 'response' });
  }

  update(kyc: IKyc): Observable<EntityResponseType> {
    return this.http.put<IKyc>(`${this.resourceUrl}/${this.getKycIdentifier(kyc)}`, kyc, { observe: 'response' });
  }

  partialUpdate(kyc: PartialUpdateKyc): Observable<EntityResponseType> {
    return this.http.patch<IKyc>(`${this.resourceUrl}/${this.getKycIdentifier(kyc)}`, kyc, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IKyc>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IKyc[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IKyc[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IKyc[]>()], asapScheduler)));
  }

  getKycIdentifier(kyc: Pick<IKyc, 'id'>): number {
    return kyc.id;
  }

  compareKyc(o1: Pick<IKyc, 'id'> | null, o2: Pick<IKyc, 'id'> | null): boolean {
    return o1 && o2 ? this.getKycIdentifier(o1) === this.getKycIdentifier(o2) : o1 === o2;
  }

  addKycToCollectionIfMissing<Type extends Pick<IKyc, 'id'>>(kycCollection: Type[], ...kycsToCheck: (Type | null | undefined)[]): Type[] {
    const kycs: Type[] = kycsToCheck.filter(isPresent);
    if (kycs.length > 0) {
      const kycCollectionIdentifiers = kycCollection.map(kycItem => this.getKycIdentifier(kycItem));
      const kycsToAdd = kycs.filter(kycItem => {
        const kycIdentifier = this.getKycIdentifier(kycItem);
        if (kycCollectionIdentifiers.includes(kycIdentifier)) {
          return false;
        }
        kycCollectionIdentifiers.push(kycIdentifier);
        return true;
      });
      return [...kycsToAdd, ...kycCollection];
    }
    return kycCollection;
  }
}
