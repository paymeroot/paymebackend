import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient, HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { KycService } from '../service/kyc.service';
import { IKyc } from '../kyc.model';
import { KycFormService } from './kyc-form.service';

import { KycUpdateComponent } from './kyc-update.component';

describe('Kyc Management Update Component', () => {
  let comp: KycUpdateComponent;
  let fixture: ComponentFixture<KycUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let kycFormService: KycFormService;
  let kycService: KycService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [KycUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(KycUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(KycUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    kycFormService = TestBed.inject(KycFormService);
    kycService = TestBed.inject(KycService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const kyc: IKyc = { id: 456 };

      activatedRoute.data = of({ kyc });
      comp.ngOnInit();

      expect(comp.kyc).toEqual(kyc);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IKyc>>();
      const kyc = { id: 123 };
      jest.spyOn(kycFormService, 'getKyc').mockReturnValue(kyc);
      jest.spyOn(kycService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ kyc });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: kyc }));
      saveSubject.complete();

      // THEN
      expect(kycFormService.getKyc).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(kycService.update).toHaveBeenCalledWith(expect.objectContaining(kyc));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IKyc>>();
      const kyc = { id: 123 };
      jest.spyOn(kycFormService, 'getKyc').mockReturnValue({ id: null });
      jest.spyOn(kycService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ kyc: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: kyc }));
      saveSubject.complete();

      // THEN
      expect(kycFormService.getKyc).toHaveBeenCalled();
      expect(kycService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IKyc>>();
      const kyc = { id: 123 };
      jest.spyOn(kycService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ kyc });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(kycService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
