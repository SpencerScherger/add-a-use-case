/* global cy, describe, beforeEach, it */
// UC-STU-deactivate-student: the backend tests cover authorization and persistence; these checks cover the
// required warning and the cancel-without-request path in the instructor UI.

const student = {
  id: 4,
  username: 'j.smith@abc.edu',
  firstName: 'John',
  lastName: 'Smith',
  email: 'j.smith@abc.edu',
  enabled: true,
  teamId: 1,
  teamName: 'Team1'
}

describe('Deactivate a student', () => {
  beforeEach(() => {
    cy.intercept('POST', '**/users/login', {
      body: {
        flag: true,
        code: 200,
        message: 'Login successfully',
        data: {
          token: 'eyJhbGciOiJub25lIn0.eyJleHAiOjQxMDQ5NjAwMDB9.',
          userInfo: {
            id: 2,
            username: 'b.gates@abc.edu',
            firstName: 'Bill',
            lastName: 'Gates',
            email: 'b.gates@abc.edu',
            enabled: true,
            roles: 'instructor',
            defaultCourseId: 1,
            defaultSectionId: 1
          }
        }
      }
    }).as('login')
    cy.login('b.gates@abc.edu', '123456')
    cy.wait('@login')
    cy.intercept('POST', '**/students/search*', {
      body: {
        flag: true,
        code: 200,
        message: 'Find students successfully',
        data: {
          content: [student],
          number: 0,
          size: 50,
          totalElements: 1,
          totalPages: 1
        }
      }
    }).as('students')
    cy.intercept('GET', '**/students/4', {
      body: { flag: true, code: 200, message: 'Find student successfully', data: student }
    }).as('student')
    cy.intercept('PATCH', '**/students/4/deactivation', {
      body: {
        flag: true,
        code: 200,
        message: 'Deactivate student successfully',
        data: { ...student, enabled: false }
      }
    }).as('deactivate')
    cy.visit('/students')
    cy.wait('@students')
    cy.get('[data-cy=edit-student-4]').click()
    cy.wait('@student')
    cy.get('[data-cy=deactivate-student]').click()
  })

  it('names every consequence before confirmation', () => {
    cy.get('.el-message-box').should('contain', 'current session')
      .and('contain', 'current team')
      .and('contain', 'reminders')
      .and('contain', 'new peer evaluations')
      .and('contain', 'weekly activity reports and peer evaluations are preserved')
      .and('contain', 'same team')
    cy.screenshot('student-deactivation-confirmation')
  })

  it('makes no request when the instructor cancels', () => {
    cy.get('.el-message-box__btns').contains('Cancel').click()
    cy.get('.el-message-box').should('not.exist')
    cy.get('@deactivate.all').should('have.length', 0)
  })
})
