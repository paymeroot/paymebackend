import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { KycDetailComponent } from './kyc-detail.component';

describe('Kyc Management Detail Component', () => {
  let comp: KycDetailComponent;
  let fixture: ComponentFixture<KycDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [KycDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: KycDetailComponent,
              resolve: { kyc: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(KycDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(KycDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load kyc on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', KycDetailComponent);

      // THEN
      expect(instance.kyc()).toEqual(expect.objectContaining({ id: 123 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
